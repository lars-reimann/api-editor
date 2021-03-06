import { Box, Heading, HStack, Link as ChakraLink, Stack, Text as ChakraText, Wrap } from '@chakra-ui/react';
import React from 'react';
import { AnnotationDropdown } from '../../annotations/AnnotationDropdown';
import { AnnotationView } from '../../annotations/AnnotationView';
import { PythonParameter } from '../model/PythonParameter';
import { DocumentationText } from './DocumentationText';
import { CompleteButton } from '../../annotations/CompleteButton';
import { Link } from 'react-router-dom';
import { MissingAnnotationButton } from '../../annotations/MissingAnnotationButton';
import { DataCopyButtons } from '../../annotations/DataCopyButtons';

interface ParameterNodeProps {
    pythonParameter: PythonParameter;
    isTitle: boolean;
}

export const ParameterNode: React.FC<ParameterNodeProps> = function ({ isTitle, pythonParameter }) {
    const id = pythonParameter.id;
    const canBeAnnotated = pythonParameter.isPublic && pythonParameter.isExplicitParameter();

    return (
        <Stack spacing={4}>
            <HStack alignItems="start">
                {isTitle ? (
                    <Heading as="h3" size="lg">
                        {pythonParameter.getUniqueName()} {!pythonParameter.isPublic && '(private)'}
                    </Heading>
                ) : (
                    <Heading as="h4" size="sm">
                        <ChakraLink as={Link} to={`/${id}`}>
                            {pythonParameter.getUniqueName()} {!pythonParameter.isPublic && '(private)'}
                        </ChakraLink>
                    </Heading>
                )}
                <Wrap>
                    {canBeAnnotated && (
                        <AnnotationDropdown
                            target={id}
                            showBoundary
                            showDescription
                            showEnum
                            showExpert
                            showRename
                            showTodo
                            showValue
                        />
                    )}

                    <CompleteButton target={id} />
                    {canBeAnnotated && <MissingAnnotationButton target={id} />}
                    <DataCopyButtons target={id} />
                </Wrap>
            </HStack>

            <AnnotationView target={id} />

            <Box paddingLeft={4}>
                {pythonParameter.description ? (
                    <DocumentationText declaration={pythonParameter} inputText={pythonParameter?.description} />
                ) : (
                    <ChakraText color="gray.500">There is no documentation for this parameter.</ChakraText>
                )}
            </Box>
        </Stack>
    );
};
