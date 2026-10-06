#!/usr/bin/env python3
"""
Achica las fotos que ya están subidas de los proyectos del portfolio.

Las fotos originales de cámara pesan 2-6 MB (4000+ px) y se muestran a menos de 600 px:
el sitio baja cientos de MB para dibujar unas pocas miniaturas. Este script descarga cada
foto, la reduce (lado mayor 1800 px, JPEG calidad 80), la sube de nuevo por
POST /api/admin/uploads y actualiza el proyecto con las URLs nuevas.

Es seguro por diseño:
  * Sin --apply es un ensayo: descarga y achica en una carpeta temporal, muestra cuánto se
    ahorraría y NO sube nada ni necesita contraseña.
  * Nunca borra ni pisa los archivos viejos de R2: las URLs viejas siguen funcionando.
  * Guarda el mapa viejo -> nuevo en <workdir>/mapping.json, así se puede reanudar si se corta
    y deshacer con --rollback.
  * Si una foto no mejora al menos un 15 % (o cambiaría su orientación), la deja como está.
  * Los proyectos sin fecha (projectDate) no se pueden actualizar: el backend exige ese campo
    en el PUT. Se avisan y se saltean ANTES de subir nada; cargales la fecha en /admin.

Uso (macOS: usa `sips`, que ya viene con el sistema):
    python3 scripts/optimize_project_images.py                       # ensayo con todo
    python3 scripts/optimize_project_images.py --only Murature       # ensayo con un proyecto
    ADMIN_PASSWORD=... python3 scripts/optimize_project_images.py --apply --only Murature
    ADMIN_PASSWORD=... python3 scripts/optimize_project_images.py --apply
    ADMIN_PASSWORD=... python3 scripts/optimize_project_images.py --rollback

La contraseña va solo por variable de entorno (nunca como argumento: queda en el historial).
Conviene correrlo después de desplegar el cambio que agrega Cache-Control a las subidas.
"""
import argparse
import json
import os
import subprocess
import sys
import tempfile
import urllib.error
import urllib.request
import uuid

DEFAULT_API = "https://fiflip-backend-production.up.railway.app"
MAX_SIDE = 1800
JPEG_QUALITY = 80
MIN_SAVING = 0.15  # no vale la pena subir una foto que no baja al menos esto


def request_json(method, url, body=None, token=None):
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    if data is not None:
        req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(req, timeout=60) as res:
            raw = res.read()
            return json.loads(raw) if raw else None
    except urllib.error.HTTPError as e:
        raise SystemExit(f"{method} {url} -> HTTP {e.code}: {e.read().decode(errors='replace')[:300]}")


def download(url, dest):
    req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(req, timeout=120) as res, open(dest, "wb") as f:
        while chunk := res.read(1 << 20):
            f.write(chunk)


def sips_props(path):
    out = subprocess.run(
        ["sips", "-g", "pixelWidth", "-g", "pixelHeight", "-g", "orientation", path],
        capture_output=True, text=True, check=True,
    )
    props = {}
    for line in out.stdout.splitlines()[1:]:
        key, _, value = line.strip().partition(": ")
        props[key] = value
    return int(props["pixelWidth"]), int(props["pixelHeight"]), props.get("orientation", "<nil>")


def shrink(src, dest):
    """Escribe la versión achicada en `dest` y devuelve True si vale la pena usarla."""
    width, height, orientation = sips_props(src)
    cmd = ["sips"]
    if max(width, height) > MAX_SIDE:  # `sips -Z` también AGRANDA las imágenes chicas
        cmd += ["-Z", str(MAX_SIDE)]
    cmd += ["-s", "format", "jpeg", "-s", "formatOptions", str(JPEG_QUALITY), src, "--out", dest]
    subprocess.run(cmd, capture_output=True, check=True)
    if sips_props(dest)[2] != orientation:  # se rotaría distinto en pantalla: mejor no tocarla
        return False
    return os.path.getsize(dest) <= os.path.getsize(src) * (1 - MIN_SAVING)


def upload(api, token, path):
    boundary = uuid.uuid4().hex
    head = (
        f"--{boundary}\r\n"
        f'Content-Disposition: form-data; name="file"; filename="{os.path.basename(path)}"\r\n'
        "Content-Type: image/jpeg\r\n\r\n"
    ).encode()
    with open(path, "rb") as f:
        body = head + f.read() + f"\r\n--{boundary}--\r\n".encode()
    req = urllib.request.Request(f"{api}/api/admin/uploads", data=body, method="POST")
    req.add_header("Content-Type", f"multipart/form-data; boundary={boundary}")
    req.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(req, timeout=120) as res:
            return json.loads(res.read())["url"]
    except urllib.error.HTTPError as e:
        raise SystemExit(f"upload de {path} -> HTTP {e.code}: {e.read().decode(errors='replace')[:300]}")


def project_photo_urls(project):
    urls = [project["coverImageUrl"]] + project["beforeImageUrls"] + project["afterImageUrls"]
    return list(dict.fromkeys(u for u in urls if u))  # sin repetidos, en orden


def project_body(project, url_map):
    """El cuerpo del PUT: el mismo proyecto, con cada URL reemplazada si tiene versión nueva."""
    swap = lambda u: url_map.get(u, u)
    return {
        "title": project["title"],
        "description": project["description"],
        "category": project["category"],
        "coverImageUrl": swap(project["coverImageUrl"]),
        "beforeImageUrls": [swap(u) for u in project["beforeImageUrls"]],
        "afterImageUrls": [swap(u) for u in project["afterImageUrls"]],
        "status": project.get("status"),
        "tea": project.get("tea"),
        "teaProjected": project.get("teaProjected"),
        "projectDate": (project.get("projectDate") or "")[:7],  # "2026-08-01" -> "2026-08"
    }


def login(api):
    password = os.environ.get("ADMIN_PASSWORD")
    if not password:
        raise SystemExit("Falta la variable de entorno ADMIN_PASSWORD (la contraseña del admin).")
    return request_json("POST", f"{api}/api/admin/login", {"password": password})["token"]


def mb(n):
    return f"{n / 1024 / 1024:6.1f} MB"


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--api", default=os.environ.get("API_URL", DEFAULT_API))
    ap.add_argument("--apply", action="store_true", help="sube las fotos y actualiza los proyectos (sin esto es un ensayo)")
    ap.add_argument("--rollback", action="store_true", help="vuelve a poner las URLs viejas en los proyectos")
    ap.add_argument("--only", help="solo los proyectos cuyo título contenga este texto")
    ap.add_argument("--workdir", default=os.path.join(tempfile.gettempdir(), "fiflip-image-optimize"))
    args = ap.parse_args()
    api = args.api.rstrip("/")

    os.makedirs(args.workdir, exist_ok=True)
    mapping_path = os.path.join(args.workdir, "mapping.json")
    mapping = json.load(open(mapping_path)) if os.path.exists(mapping_path) else {}  # viejo -> nuevo

    projects = request_json("GET", f"{api}/api/projects")
    if args.only:
        projects = [p for p in projects if args.only.lower() in p["title"].lower()]
        if not projects:
            raise SystemExit(f'Ningún proyecto contiene "{args.only}".')

    if args.rollback:
        token = login(api)
        reverse = {new: old for old, new in mapping.items()}
        for p in projects:
            body = project_body(p, reverse)
            if not any(u in reverse for u in project_photo_urls(p)):
                continue
            request_json("PUT", f"{api}/api/admin/projects/{p['id']}", body, token)
            print(f"revertido: {p['title'].strip()}")
        return

    token = login(api) if args.apply else None
    total_before = total_after = 0
    skipped = []
    print(f"{'PROYECTO':22} {'FOTOS':>5} {'ANTES':>10} {'DESPUÉS':>10}")
    for p in projects:
        title = p["title"].strip()
        if args.apply and not p.get("projectDate"):
            skipped.append(title)
            continue  # el PUT lo rechazaría: ni siquiera subimos sus fotos
        before = after = 0
        for url in project_photo_urls(p):
            src = os.path.join(args.workdir, "orig-" + uuid.uuid5(uuid.NAMESPACE_URL, url).hex)
            out = os.path.join(args.workdir, "opt-" + uuid.uuid5(uuid.NAMESPACE_URL, url).hex + ".jpg")
            if not os.path.exists(src):
                download(url, src)
            size = os.path.getsize(src)
            before += size
            if url in mapping:  # ya procesada en una corrida anterior
                after += os.path.getsize(out) if os.path.exists(out) else size
                continue
            if shrink(src, out):
                after += os.path.getsize(out)
                if args.apply:
                    mapping[url] = upload(api, token, out)
                    json.dump(mapping, open(mapping_path, "w"), indent=1)  # guarda de a una: se puede reanudar
            else:
                after += size
        total_before += before
        total_after += after
        print(f"{title[:22]:22} {len(project_photo_urls(p)):>5} {mb(before)} {mb(after)}")
        if args.apply and any(u in mapping for u in project_photo_urls(p)):
            request_json("PUT", f"{api}/api/admin/projects/{p['id']}", project_body(p, mapping), token)

    print(f"{'TOTAL':22} {'':>5} {mb(total_before)} {mb(total_after)}   (-{100 - 100 * total_after / max(total_before, 1):.0f} %)")
    if not args.apply:
        print("\nEnsayo: no se subió ni cambió nada. Para aplicar, agregá --apply (y ADMIN_PASSWORD).")
        undated = [p["title"].strip() for p in projects if not p.get("projectDate")]
        if undated:
            print(f"Ojo: sin fecha en /admin, no se podrán actualizar: {', '.join(undated)}")
    if skipped:
        print(f"\nSALTEADOS por no tener fecha (cargasela en /admin y volvé a correr): {', '.join(skipped)}")


if __name__ == "__main__":
    sys.exit(main())
