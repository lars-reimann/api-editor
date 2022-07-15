import inspect
from typing import Union

import astroid


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
