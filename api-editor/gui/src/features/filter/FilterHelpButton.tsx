import {
    Box,
    Icon,
    IconButton,
    ListItem,
    Popover,
    PopoverArrow,
    PopoverBody,
    PopoverCloseButton,
    PopoverContent,
    PopoverHeader,
    PopoverTrigger,
    Text as ChakraText,
    UnorderedList,
} from '@chakra-ui/react';
import React from 'react';

export const FilterHelpButton = function () {
    return (
        <Box zIndex={100}>
            <Popover isLazy={true} placement="top">
                <PopoverTrigger>
                    <IconButton variant="ghost" icon={<Icon name="help" />} aria-label="help" />
                </PopoverTrigger>
                <PopoverContent minWidth={600} fontSize="sm" marginRight={2}>
                    <PopoverArrow />
                    <PopoverCloseButton />
                    <PopoverHeader>Filter Options</PopoverHeader>
                    <PopoverBody maxHeight="50vh" overflow="auto">
                        <UnorderedList spacing={2}>
                            <ListItem>
                                <ChakraText>
                                    <strong>is:[type]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that are of the given type. Replace [type] with one of{' '}
                                    <em>module, class, function, parameter</em>.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>is:[visibility]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that have the given visibility. Replace [visibility] with one
                                    of <em>public, internal</em>.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>is:done</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that are marked as complete and where all annotations are
                                    marked as correct.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>is:complete</strong>
                                </ChakraText>
                                <ChakraText>Displays only elements that are marked as complete.</ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>is:[requiredOrOptional]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only parameters that either required or optional. Replace
                                    [requiredOrOptional] with one of <em>required, optional</em>.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>is:[assignedBy]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only parameters that are assigned in the given manner. Replace [assignedBy]
                                    with one of <em>implicit, positionOnly, positionOrName, nameOnly</em>.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>name:[operator][string]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements with matching names (case-sensitive). Replace [operator] with{' '}
                                    <em>=</em> to display only elements that match [string] exactly or with <em>~</em>{' '}
                                    to display only elements that contain [string] as a substring.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>name:/[regex]/</strong>
                                </ChakraText>
                                <ChakraText>Displays only elements with names that match the given [regex].</ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>qname:[operator][string]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements with matching qualified names (case-sensitive). Replace
                                    [operator] with <em>=</em> to display only elements that match [string] exactly or
                                    with <em>~</em> to display only elements that contain [string] as a substring.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>qname:/[regex]/</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements with qualified names that match the given [regex].
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>annotation:any</strong>
                                </ChakraText>
                                <ChakraText>Displays only elements that have been annotated.</ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>annotation:[type]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that are annotated with the given type xy. Replace [type]
                                    with one of{' '}
                                    <em>
                                        @attribute, @boundary, @calledAfter, @constant, @description, @enum, @group,
                                        @move, @optional, @pure, @remove, @renaming, @required, @todo
                                    </em>
                                    .
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>usages:[operator][expected]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that are used a certain number of times. Replace [operator]
                                    with one of <em>&lt;, &lt;=, =, &gt;=, &gt;</em>. Replace [expected] with the
                                    expected number of usages.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>usefulness:[operator][expected]</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that have a certain usefulness. Replace [operator] with one
                                    of <em>&lt;, &lt;=, =, &gt;=, &gt;</em>. Replace [expected] with the expected
                                    usefulness.
                                </ChakraText>
                            </ListItem>
                            <ListItem>
                                <ChakraText>
                                    <strong>!filter</strong>
                                </ChakraText>
                                <ChakraText>
                                    Displays only elements that do not match the given filter. Possible filters are any
                                    in this list.
                                </ChakraText>
                            </ListItem>
                        </UnorderedList>
                    </PopoverBody>
                </PopoverContent>
            </Popover>
        </Box>
    );
};
