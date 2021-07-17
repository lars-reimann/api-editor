import {IconDefinition} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import classNames from "classnames";
import React, {useState} from "react";
import {useLocation} from "react-router";
import {useHistory} from "react-router-dom";
import PythonDeclaration from "../../model/python/PythonDeclaration";
import {ChildrenProp} from "../../util/types";
import VisibilityIndicator from "../Util/VisibilityIndicator";
import TreeNodeCSS from "./TreeNode.module.css";

interface TreeNodeProps extends ChildrenProp {
    declaration: PythonDeclaration
    icon: IconDefinition
    isExpandable: boolean
    isWorthClicking: boolean
}

export default function TreeNode(props: TreeNodeProps): JSX.Element {
    const [showChildren, setShowChildren] = useState(selfOrChildIsSelected(props.declaration));
    const history = useHistory();

    const className = classNames({
        [TreeNodeCSS.selected]: isSelected(props.declaration),
        "text-muted": !props.isWorthClicking
    });

    const level = levelOf(props.declaration);
    const style = {
        paddingLeft: (level === 0 ? '1rem' : `calc(0.5 * ${level} * (1.25em + 0.25rem) + 1rem)`)
    };

    const handleClick = () => {
        setShowChildren(prevState => !prevState);
        history.push(`/${props.declaration.path().join("/")}`);
    };

    return (
        <div className={TreeNodeCSS.treeNode}>
            <div className={className} style={style} onClick={handleClick}>
                <VisibilityIndicator
                    className={TreeNodeCSS.icon}
                    hasChildren={props.isExpandable}
                    showChildren={showChildren}
                    isSelected={isSelected(props.declaration)}
                />
                <FontAwesomeIcon
                    className={TreeNodeCSS.icon}
                    icon={props.icon}
                    fixedWidth
                />
                {props.declaration.name}
            </div>
            <div className={TreeNodeCSS.children}>
                {showChildren && props.children}
            </div>
        </div>
    );
}

function levelOf(declaration: PythonDeclaration): number {
    return declaration.path().length - 2;
}

function isSelected(declaration: PythonDeclaration): boolean {
    return `/${declaration.path().join("/")}` === useLocation().pathname;
}

function selfOrChildIsSelected(declaration: PythonDeclaration): boolean {
    const declarationPath = `/${declaration.path().join("/")}`;
    const currentPath = useLocation().pathname;

    // The slash prevents /sklearn/sklearn from opening when the path is /sklearn/sklearn.base
    return currentPath === declarationPath || currentPath.startsWith(`${declarationPath}/`);
}
