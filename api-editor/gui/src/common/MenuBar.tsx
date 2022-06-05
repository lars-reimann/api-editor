import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Box,
    Button,
    Flex,
    Heading,
    HStack,
    Icon,
    Input,
    Menu,
    MenuButton,
    MenuDivider,
    MenuGroup,
    MenuItem,
    MenuItemOption,
    MenuList,
    MenuOptionGroup,
    Spacer,
    Text as ChakraText,
    useColorMode,
    VStack,
} from '@chakra-ui/react';
import React, { useRef, useState } from 'react';
import { FaChevronDown } from 'react-icons/fa';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { resetAnnotations, toggleAnnotationImportDialog } from '../features/annotations/annotationSlice';
import AnnotatedPythonPackageBuilder from '../features/annotatedPackageData/model/AnnotatedPythonPackageBuilder';
import PythonPackage from '../features/packageData/model/PythonPackage';
import { HeatMapMode, setHeatMapMode, togglePackageDataImportDialog } from '../features/packageData/packageDataSlice';
import { Setter } from './util/types';
import { toggleUsageImportDialog } from '../features/usages/usageSlice';
import { FilterHelpButton } from './FilterHelpButton';

interface MenuBarProps {
    pythonPackage: PythonPackage;
    filter: string;
    setFilter: Setter<string>;
    displayInferErrors: (errors: string[]) => void;
}

const DeleteAllAnnotations = function () {
    const dispatch = useAppDispatch();
    const [isOpen, setIsOpen] = useState(false);
    const cancelRef = useRef(null);

    // Event handlers ----------------------------------------------------------

    const handleConfirm = () => {
        dispatch(resetAnnotations());
        setIsOpen(false);
    };
    const handleCancel = () => setIsOpen(false);

    // Render ------------------------------------------------------------------

    return (
        <>
            <Button onClick={() => setIsOpen(true)}>Delete all annotations</Button>

            <AlertDialog isOpen={isOpen} leastDestructiveRef={cancelRef} onClose={handleCancel}>
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <Heading>Delete all annotations</Heading>
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            <VStack alignItems="flexStart">
                                <ChakraText>Are you sure? You can't undo this action afterwards.</ChakraText>
                                <ChakraText>
                                    Hint: Consider exporting your work first by clicking on the "Export" button in the
                                    menu bar.
                                </ChakraText>
                            </VStack>
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={handleCancel}>
                                Cancel
                            </Button>
                            <Button colorScheme="red" onClick={handleConfirm} ml={3}>
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    );
};

export const MenuBar: React.FC<MenuBarProps> = function ({ pythonPackage, filter, setFilter, displayInferErrors }) {
    const { colorMode, toggleColorMode } = useColorMode();
    const dispatch = useAppDispatch();

    const annotationStore = useAppSelector((state) => state.annotations);

    const exportAnnotations = () => {
        const a = document.createElement('a');
        const file = new Blob([JSON.stringify(annotationStore)], {
            type: 'application/json',
        });
        a.href = URL.createObjectURL(file);
        a.download = 'annotations.json';
        a.click();
    };

    const infer = () => {
        const annotatedPythonPackageBuilder = new AnnotatedPythonPackageBuilder(pythonPackage, annotationStore);
        const annotatedPythonPackage = annotatedPythonPackageBuilder.generateAnnotatedPythonPackage();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(annotatedPythonPackage),
        };
        fetch('/api-editor/infer', requestOptions).then(async (response) => {
            if (!response.ok) {
                const jsonResponse = await response.json();
                displayInferErrors(jsonResponse);
            } else {
                const jsonBlob = await response.blob();
                const a = document.createElement('a');
                a.href = URL.createObjectURL(jsonBlob);
                a.download = 'simpleml.zip';
                a.click();
            }
        });
    };

    const settings: string[] = [];
    if (colorMode === 'dark') {
        settings.push('darkMode');
    }

    return (
        <Flex as="nav" borderBottom={1} layerStyle="subtleBorder" padding="0.5em 1em">
            <HStack>
                {/* Box gets rid of popper.js warning "CSS margin styles cannot be used" */}
                <Box>
                    <Menu>
                        <MenuButton as={Button} rightIcon={<Icon as={FaChevronDown} />}>
                            File
                        </MenuButton>
                        <MenuList>
                            <MenuGroup title="Import">
                                <MenuItem paddingLeft={8} onClick={() => dispatch(togglePackageDataImportDialog())}>
                                    API Data
                                </MenuItem>
                                <MenuItem paddingLeft={8} onClick={() => dispatch(toggleUsageImportDialog())}>
                                    Usages
                                </MenuItem>
                                <MenuItem paddingLeft={8} onClick={() => dispatch(toggleAnnotationImportDialog())}>
                                    Annotations
                                </MenuItem>
                            </MenuGroup>
                            <MenuDivider />
                            <MenuGroup title="Export">
                                <MenuItem paddingLeft={8} onClick={exportAnnotations}>
                                    Annotations
                                </MenuItem>
                            </MenuGroup>
                        </MenuList>
                    </Menu>
                </Box>

                <Button onClick={infer}>Generate adapters</Button>
                <DeleteAllAnnotations />

                <Box>
                    <Menu closeOnSelect={false}>
                        <MenuButton as={Button} rightIcon={<Icon as={FaChevronDown} />}>
                            Settings
                        </MenuButton>
                        <MenuList>
                            <MenuOptionGroup type="checkbox" value={settings}>
                                <MenuItemOption value={'darkMode'} onClick={toggleColorMode}>
                                    Dark mode
                                </MenuItemOption>
                            </MenuOptionGroup>
                            <MenuDivider />
                            <MenuGroup title="Heat Map Mode">
                                <MenuOptionGroup type="radio" defaultValue="none">
                                    <MenuItemOption
                                        paddingLeft={8}
                                        value={'none'}
                                        onClick={() => dispatch(setHeatMapMode(HeatMapMode.None))}
                                    >
                                        None
                                    </MenuItemOption>
                                    <MenuItemOption
                                        paddingLeft={8}
                                        value={'usages'}
                                        onClick={() => dispatch(setHeatMapMode(HeatMapMode.Usages))}
                                    >
                                        Usages
                                    </MenuItemOption>
                                    <MenuItemOption
                                        paddingLeft={8}
                                        value={'usefulness'}
                                        onClick={() => dispatch(setHeatMapMode(HeatMapMode.Usefulness))}
                                    >
                                        Usefulness
                                    </MenuItemOption>
                                    <MenuItemOption
                                        paddingLeft={8}
                                        value={'annotations'}
                                        onClick={() => dispatch(setHeatMapMode(HeatMapMode.Annotations))}
                                    >
                                        Annotations
                                    </MenuItemOption>
                                </MenuOptionGroup>
                            </MenuGroup>
                        </MenuList>
                    </Menu>
                </Box>
            </HStack>

            <Spacer />

            <HStack>
                <Input
                    type="text"
                    placeholder="Filter..."
                    value={filter}
                    onChange={(event) => setFilter(event.target.value)}
                    spellCheck={false}
                    minWidth="400px"
                />
                <FilterHelpButton />
            </HStack>
        </Flex>
    );
};
