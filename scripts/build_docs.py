"""Copy the READMEs into .docs-build/ for MkDocs and generate the sidebar from the root README."""
import re
import shutil
import yaml
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / ".docs-build"
LINK = re.compile(r"\[([^\]]+)\]\(([^)]+/README\.md)\)")
HOME_SKIP = {"## 🎉Stargazers", "## 🎉Forkers"}


def home_page(text: str) -> str:
    lines, skip = [], False
    for line in text.splitlines():
        if line.startswith("## "):
            skip = line in HOME_SKIP
        if not skip:
            lines.append(line)
    return "\n".join(lines) + "\n"


def nav_from_readme(text: str) -> str:
    root: list = ["index.md"]
    stack = [(-1, root)]
    for line in text.splitlines():
        m = re.match(r"^( *)([+-]) (.*)$", line)
        if not m:
            continue
        depth, marker, body = len(m.group(1)), m.group(2), m.group(3).strip()
        link = LINK.match(body)
        if not link and marker != "+":
            continue
        while stack[-1][0] >= depth:
            stack.pop()
        items = stack[-1][1]
        if link:
            items.append(link.group(2))
        else:
            children: list = []
            items.append({body: children})
            stack.append((depth, children))
    prune(root)
    root.append("**")
    return yaml.safe_dump({"nav": root}, allow_unicode=True, sort_keys=False)


def prune(items: list) -> None:
    for item in list(items):
        if isinstance(item, dict):
            children = next(iter(item.values()))
            prune(children)
            if not children:
                items.remove(item)


def main() -> None:
    shutil.rmtree(OUT, ignore_errors=True)
    OUT.mkdir()
    readme = (ROOT / "README.md").read_text(encoding="utf-8")
    (OUT / "index.md").write_text(home_page(readme), encoding="utf-8")
    (OUT / ".nav.yml").write_text(nav_from_readme(readme), encoding="utf-8")
    shutil.copytree(ROOT / "image", OUT / "image")
    for src in ROOT.glob("spring-*/*/README.md"):
        dst = OUT / src.relative_to(ROOT)
        dst.parent.mkdir(parents=True)
        text = src.read_text(encoding="utf-8")
        title = next((l[3:].strip() for l in text.splitlines() if l.startswith("## ")), src.parent.name)
        text = text.replace("](/spring-", "](../../spring-")
        dst.write_text(f"---\ntitle: {title}\n---\n\n{text}", encoding="utf-8")


if __name__ == "__main__":
    main()
