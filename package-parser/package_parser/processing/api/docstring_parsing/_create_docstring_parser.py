from ._DocstringStyle import DocstringStyle
from ._EpydocParser import EpydocParser
from ._NumpyDocParser import NumpyDocParser
from ._PlaintextDocstringParser import PlaintextDocstringParser


def create_docstring_parser(style: DocstringStyle):
    if style == DocstringStyle.NUMPY:
        return NumpyDocParser()
    if style == DocstringStyle.EPYDOC:
        return EpydocParser()
    else:  # TODO: cover other cases
        return PlaintextDocstringParser()
