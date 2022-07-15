import json
from pathlib import Path
from typing import Optional

from package_parser.cli._json_encoder import CustomEncoder
from package_parser.cli._shared_constants import _API_KEY
from package_parser.processing.api import get_api
from package_parser.processing.api.docstring_parsing import DocstringStyle
from package_parser.processing.api.model import API
from package_parser.processing.dependencies import get_dependencies
from package_parser.utils import ensure_file_exists


def _run_api_command(
    package: str,
    src_dir_path: Path,
    out_dir_path: Path,
    docstring_style: DocstringStyle,
    result_dict: Optional[dict] = None,
) -> None:
    api = get_api(package, src_dir_path)
    api_dependencies = get_dependencies(api)

    api_file_path = _write_api_file(api, out_dir_path)
    _write_api_dependency_file(api, api_dependencies, out_dir_path)

    if result_dict is not None:
        result_dict[_API_KEY] = api_file_path


def _write_api_file(api: API, out_dir_path: Path) -> Path:
    out_file_api = out_dir_path.joinpath(f"{api.package}__api.json")
    ensure_file_exists(out_file_api)
    with out_file_api.open("w") as f:
        json.dump(api.to_json(), f, indent=2, cls=CustomEncoder)
    return out_file_api


def _write_api_dependency_file(api: API, api_dependencies, out):
    out_file_api_dependencies = out.joinpath(f"{api.package}__api_dependencies.json")
    ensure_file_exists(out_file_api_dependencies)
    with out_file_api_dependencies.open("w") as f:
        json.dump(api_dependencies.to_json(), f, indent=2, cls=CustomEncoder)
