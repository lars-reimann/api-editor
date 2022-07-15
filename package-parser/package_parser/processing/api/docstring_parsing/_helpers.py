import inspect
from typing import Union

import astroid
from docstring_parser import Docstring


def get_full_docstring(
    declaration: Union[astroid.ClassDef, astroid.FunctionDef]
) -> str:
    """
    Returns the full docstring of the given declaration or an empty string if no docstring is available. Indentation is
    cleaned up.
    """

    doc_node = declaration.doc_node
    if doc_node is None:
        return ""
    return inspect.cleandoc(doc_node.value)


def get_description(docstring_obj: Docstring) -> str:
    """
    Returns the concatenated short and long description of the given docstring object or an empty string if these parts
    are blank.
    """

    summary: str = docstring_obj.short_description or ""
    extended_summary: str = docstring_obj.long_description or ""

    result = ""
    result += summary.rstrip()
    result += "\n\n"
    result += extended_summary.rstrip()
    return result.strip()
