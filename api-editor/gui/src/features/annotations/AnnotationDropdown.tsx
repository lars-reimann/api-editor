import { Box, Button, Icon, Menu, MenuButton, MenuGroup, MenuItem, MenuList } from '@chakra-ui/react';
import React from 'react';
import { FaChevronDown } from 'react-icons/fa';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { selectComplete, selectUsernameIsValid } from './annotationSlice';
import {
    showBoundaryAnnotationForm,
    showCalledAfterAnnotationForm,
    showDescriptionAnnotationForm,
    showEnumAnnotationForm,
    showExpertAnnotationForm,
    showGroupAnnotationForm,
    showMoveAnnotationForm,
    showPureAnnotationForm,
    showRemoveAnnotationForm,
    showRenameAnnotationForm,
    showTodoAnnotationForm,
    showValueAnnotationForm,
} from '../ui/uiSlice';

interface AnnotationDropdownProps {
    showBoundary?: boolean;
    showCalledAfter?: boolean;
    showDescription?: boolean;
    showEnum?: boolean;
    showExpert?: boolean;
    showGroup?: boolean;
    showMove?: boolean;
    showPure?: boolean;
    showRename?: boolean;
    showRemove?: boolean;
    showTodo?: boolean;
    showValue?: boolean;
    target: string;
}

export const AnnotationDropdown: React.FC<AnnotationDropdownProps> = function ({
    showBoundary = false,
    showCalledAfter = false,
    showDescription = false,
    showGroup = false,
    showEnum = false,
    showExpert = false,
    showMove = false,
    showPure = false,
    showRename = false,
    showRemove = false,
    showTodo = false,
    showValue = false,
    target,
}) {
    const dispatch = useAppDispatch();
    const isComplete = Boolean(useAppSelector(selectComplete(target)));
    const isValidUsername = Boolean(useAppSelector(selectUsernameIsValid));
    const isDisabled = isComplete || !isValidUsername;

    // Render ----------------------------------------------------------------------------------------------------------
    return (
        // Box gets rid of popper.js warning "CSS margin styles cannot be used"
        <Box>
            <Menu>
                <MenuButton
                    as={Button}
                    size="sm"
                    variant="outline"
                    rightIcon={<Icon as={FaChevronDown} />}
                    disabled={isDisabled}
                >
                    Annotations
                </MenuButton>
                <MenuList>
                    {(showEnum || showBoundary) && (
                        <MenuGroup title="Type">
                            {showBoundary && (
                                <MenuItem onClick={() => dispatch(showBoundaryAnnotationForm(target))} paddingLeft={8}>
                                    @boundary
                                </MenuItem>
                            )}
                            {showEnum && (
                                <MenuItem onClick={() => dispatch(showEnumAnnotationForm(target))} paddingLeft={8}>
                                    @enum
                                </MenuItem>
                            )}
                        </MenuGroup>
                    )}

                    {(showCalledAfter ||
                        showDescription ||
                        showExpert ||
                        showGroup ||
                        showMove ||
                        showPure ||
                        showRemove ||
                        showRename ||
                        showTodo ||
                        showValue) && (
                        <MenuGroup title="Uncategorized">
                            {showCalledAfter && (
                                <MenuItem
                                    onClick={() =>
                                        dispatch(
                                            showCalledAfterAnnotationForm({
                                                target,
                                                calledAfterName: '',
                                            }),
                                        )
                                    }
                                    paddingLeft={8}
                                >
                                    @calledAfter
                                </MenuItem>
                            )}
                            {showDescription && (
                                <MenuItem
                                    onClick={() => dispatch(showDescriptionAnnotationForm(target))}
                                    paddingLeft={8}
                                >
                                    @description
                                </MenuItem>
                            )}
                            {showExpert && (
                                <MenuItem onClick={() => dispatch(showExpertAnnotationForm(target))} paddingLeft={8}>
                                    @expert
                                </MenuItem>
                            )}
                            {showGroup && (
                                <MenuItem
                                    onClick={() =>
                                        dispatch(
                                            showGroupAnnotationForm({
                                                target,
                                                groupName: '',
                                            }),
                                        )
                                    }
                                    paddingLeft={8}
                                >
                                    @group
                                </MenuItem>
                            )}
                            {showMove && (
                                <MenuItem onClick={() => dispatch(showMoveAnnotationForm(target))} paddingLeft={8}>
                                    @move
                                </MenuItem>
                            )}
                            {showPure && (
                                <MenuItem onClick={() => dispatch(showPureAnnotationForm(target))} paddingLeft={8}>
                                    @pure
                                </MenuItem>
                            )}
                            {showRemove && (
                                <MenuItem onClick={() => dispatch(showRemoveAnnotationForm(target))} paddingLeft={8}>
                                    @remove
                                </MenuItem>
                            )}
                            {showRename && (
                                <MenuItem onClick={() => dispatch(showRenameAnnotationForm(target))} paddingLeft={8}>
                                    @rename
                                </MenuItem>
                            )}
                            {showTodo && (
                                <MenuItem onClick={() => dispatch(showTodoAnnotationForm(target))} paddingLeft={8}>
                                    @todo
                                </MenuItem>
                            )}
                            {showValue && (
                                <MenuItem onClick={() => dispatch(showValueAnnotationForm(target))} paddingLeft={8}>
                                    @value
                                </MenuItem>
                            )}
                        </MenuGroup>
                    )}
                </MenuList>
            </Menu>
        </Box>
    );
};
