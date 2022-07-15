import multiprocessing
from functools import partial
from pathlib import Path

from package_parser.cli._run_annotations import _run_annotations
from package_parser.cli._run_api import _run_api_command
from package_parser.cli._run_usages import _run_usages_command
from package_parser.cli._shared_constants import _API_KEY, _USAGES_KEY
from package_parser.processing.api.documentation_parsing import DocstringStyle


def _run_all_command(
    package: str,
    src_dir_path: Path,
    client_dir_path: Path,
    out_dir_path: Path,
    docstring_style: DocstringStyle,
    n_processes: int,
    batch_size: int,
) -> None:
    out_file_annotations = out_dir_path.joinpath("annotations.json")
    results = _run_in_parallel(
        partial(_run_api_command, package, src_dir_path, out_dir_path),
        partial(
            _run_usages_command,
            package,
            client_dir_path,
            out_dir_path,
            n_processes,
            batch_size,
        ),
    )
    _run_annotations(results[_API_KEY], results[_USAGES_KEY], out_file_annotations)


def _run_in_parallel(*fns) -> dict:
    manager = multiprocessing.Manager()
    return_dict: dict[str, str] = manager.dict()
    proc = []
    for fn in fns:
        p = multiprocessing.Process(target=fn, args=(return_dict,))
        proc.append(p)
        p.start()

    for p in proc:
        p.join()

    return return_dict
