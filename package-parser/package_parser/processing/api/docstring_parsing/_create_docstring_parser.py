from ._DocstringStyle import DocstringStyle
from ._NumpyDocParser import NumpyDocParser
from ._PlaintextDocstringParser import PlaintextDocstringParser


def create_docstring_parser(style: DocstringStyle):
    if style == DocstringStyle.NUMPY:
        return NumpyDocParser()
    else:  # TODO: cover other cases
        return PlaintextDocstringParser()
