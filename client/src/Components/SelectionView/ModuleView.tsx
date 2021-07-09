import React from "react";
import PythonModule from "../../model/python/PythonModule";
import {isEmptyList} from "../../util/listOperations";
import ModuleImportItem from "./ModuleImportItem";
import ModuleImportFromItem from "./ModuleImportFromItem";

interface ModuleViewProps {
    pythonModule: PythonModule,
}

export default function ModuleView(props: ModuleViewProps): JSX.Element {
    return (
        <>
            <h1>{props.pythonModule.name}</h1>
            <h2>Imports</h2>
            {!isEmptyList(props.pythonModule.imports) ?
                <ul className="module-list">
                    {props.pythonModule.imports.map((pythonImport, index) => (
                        <li key={index}>
                            <ModuleImportItem inputImport={pythonImport}/>
                        </li>
                    ))}
                </ul>
                : <span className="text-muted"
                        style={{paddingLeft: '1rem'}}>There are no imports.</span>
            }
            <h2>Imported from</h2>
            {!isEmptyList(props.pythonModule.imports) ?
                <ul className="module-list">
                    {props.pythonModule.fromImports.map((pythonImportFrom, index) => (
                        <li key={index}>
                            <ModuleImportFromItem inputImportFrom={pythonImportFrom}/>
                        </li>
                    ))}
                </ul>
                : <span className="text-muted"
                          style={{paddingLeft: '1rem'}}>There are no modules that import this module.</span>}
        </>
    );
}
