import { HStack, Icon, Text as ChakraText } from '@chakra-ui/react';
import React, { MouseEvent } from 'react';
import { IconType } from 'react-icons/lib';
import { useLocation } from 'react-router';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../../app/hooks';
import { PythonDeclaration } from '../model/PythonDeclaration';
import {
    HeatMapMode,
    selectHeatMapMode,
    selectIsExpandedInTreeView,
    setAllCollapsedInTreeView,
    toggleIsExpandedInTreeView,
} from '../../ui/uiSlice';
import { VisibilityIndicator } from './VisibilityIndicator';
import { AbstractPythonFilter } from '../../filter/model/AbstractPythonFilter';
import { selectAnnotationStore } from '../../annotations/annotationSlice';
import { HeatMapInterpolation, HeatMapTag } from './HeatMapTag';
import { UsageCountStore } from '../../usages/model/UsageCountStore';

interface TreeNodeProps {
    declaration: PythonDeclaration;
    icon: IconType;
    isExpandable: boolean;
    filter: AbstractPythonFilter;
    usages: UsageCountStore;
    maxValue?: number;
    specificValue?: number;
}

export class ValuePair {
    specificValue: number | undefined;
    maxValue: number | undefined;

    constructor(specificValue: number | undefined, maxValue: number | undefined) {
        this.specificValue = specificValue;
        this.maxValue = maxValue;
    }
}

export const TreeNode: React.FC<TreeNodeProps> = function ({
    declaration,
    icon,
    isExpandable,
    filter,
    usages,
    maxValue,
    specificValue = 0,
}) {
    const currentPathname = useLocation().pathname;
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    const showChildren = useAppSelector(selectIsExpandedInTreeView(declaration.id));
    const annotations = useAppSelector(selectAnnotationStore);

    const level = levelOf(declaration);
    const paddingLeft = level === 0 ? '1rem' : `${1 + 0.75 * level}rem`;
    const backgroundColor = isSelected(declaration, currentPathname) ? 'cornflowerblue' : undefined;
    const color = isSelected(declaration, currentPathname) ? 'white' : undefined;

    const fontWeight = filter.shouldKeepDeclaration(declaration, annotations, usages) ? 'bold' : undefined;

    const toggleExpanded = () => {
        if (showChildren) {
            dispatch(setAllCollapsedInTreeView([...declaration.descendantsOrSelf()].map((d) => d.id)));
        } else {
            dispatch(toggleIsExpandedInTreeView(declaration.id));
        }
    };

    const [timeoutId, setTimeoutId] = React.useState<NodeJS.Timeout>();
    const handleNodeClick = (event: MouseEvent) => {
        if (event.detail === 1) {
            // Handle single click
            const newTimeoutId = setTimeout(() => {
                navigate(`/${declaration.id}`);
            }, 200);
            setTimeoutId(newTimeoutId);
        } else if (event.detail === 2) {
            // Handle double click
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
            toggleExpanded();
        }
    };

    const handleVisibilityIndicatorClick = (event: MouseEvent) => {
        toggleExpanded();
        event.stopPropagation();
    };

    const interpolation =
        useAppSelector(selectHeatMapMode) === HeatMapMode.Annotations
            ? HeatMapInterpolation.LINEAR
            : HeatMapInterpolation.LOGARITHMIC;
    const displayHeatMap = useAppSelector(selectHeatMapMode) !== HeatMapMode.None && maxValue !== undefined;

    return (
        <HStack
            userSelect="none"
            cursor="pointer"
            color={color}
            backgroundColor={backgroundColor}
            paddingLeft={paddingLeft}
            onClick={handleNodeClick}
        >
            <VisibilityIndicator
                hasChildren={isExpandable}
                showChildren={showChildren}
                isSelected={isSelected(declaration, currentPathname)}
                onClick={handleVisibilityIndicatorClick}
            />
            <Icon as={icon} />
            {displayHeatMap && (
                <HeatMapTag actualValue={specificValue} maxValue={maxValue} interpolation={interpolation} />
            )}
            <ChakraText fontWeight={fontWeight}>{declaration.getUniqueName()}</ChakraText>
        </HStack>
    );
};

const levelOf = function (declaration: PythonDeclaration): number {
    return declaration.id.split('/').length - 2;
};

const isSelected = function (declaration: PythonDeclaration, currentPathname: string): boolean {
    return `/${declaration.id}` === currentPathname;
};
