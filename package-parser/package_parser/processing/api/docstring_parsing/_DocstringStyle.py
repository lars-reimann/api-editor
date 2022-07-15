from enum import Enum


class DocstringStyle(Enum):
    AUTO = "auto",
    PLAINTEXT = "plaintext",
    REST = "reST",
    NUMPY = "numpy",
    GOOGLE = "google",
    EPYDOC = "epydoc"

    def __str__(self):
        return self.name

    @staticmethod
    def from_string(key: str):
        try:
            return DocstringStyle[key.upper()]
        except KeyError:
            raise ValueError(f"Unknown docstring style: {key}")
