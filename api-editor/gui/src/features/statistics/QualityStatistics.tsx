import React from 'react';
import { Box, Heading, SimpleGrid, useColorModeValue } from '@chakra-ui/react';
import { useAppSelector } from '../../app/hooks';
import { Annotation, selectAnnotationStore } from '../annotations/annotationSlice';
import { Pie } from 'react-chartjs-2';

import { ArcElement, Chart as ChartJS, Title, Tooltip } from 'chart.js';

ChartJS.register(ArcElement, Title, Tooltip);

export const QualityStatistics = function () {
    const annotationStore = useAppSelector(selectAnnotationStore);

    return (
        <>
            <Heading as="h3" size="md">
                Quality of Autogenerated Annotations
            </Heading>
            <Box width="100%">
                <SimpleGrid columns={{ base: 1, wqhd: 2 }} width="100%">
                    <QualityPieChart
                        annotationType="Boundaries"
                        annotations={Object.values(annotationStore.boundaries)}
                    />
                    <QualityPieChart
                        annotationType="Constants"
                        annotations={Object.values(annotationStore.constants)}
                    />
                    <QualityPieChart annotationType="Enums" annotations={Object.values(annotationStore.enums)} />
                    <QualityPieChart
                        annotationType="Optionals"
                        annotations={Object.values(annotationStore.optionals)}
                    />
                    <QualityPieChart annotationType="Removes" annotations={Object.values(annotationStore.removes)} />
                    <QualityPieChart
                        annotationType="Requireds"
                        annotations={Object.values(annotationStore.requireds)}
                    />
                </SimpleGrid>
            </Box>
        </>
    );
};

interface QualityPieChartProps {
    annotationType: string;
    annotations: Annotation[];
}

const QualityPieChart: React.FC<QualityPieChartProps> = function ({ annotationType, annotations }) {
    const correctBg = useColorModeValue('#38a169', '#68d391');
    const correctBorder = useColorModeValue('#2f855a', '#99e6b3');

    const changedBg = useColorModeValue('#a19038', '#d3ba68');
    const changedBorder = useColorModeValue('#857a2f', '#e6d799');

    const removedBg = useColorModeValue('#a13838', '#d36868');
    const removedBorder = useColorModeValue('#852f2f', '#e69999');

    const uncheckedBg = useColorModeValue('#CCC', '#888');
    const uncheckedBorder = useColorModeValue('#AAA', '#AAA');

    const textColor = useColorModeValue('#000', '#FFF');

    const autogeneratedAnnotations = annotations.filter((it) => (it.authors ?? []).includes('$autogen$'));
    const numberOfCorrectAnnotations = autogeneratedAnnotations.filter(
        (it) => !it.isRemoved && (it.reviewers ?? []).length > 0 && (it.authors ?? []).length <= 1,
    ).length;
    const numberOfChangedAnnotations = autogeneratedAnnotations.filter(
        (it) => !it.isRemoved && (it.reviewers ?? []).length > 0 && (it.authors ?? []).length > 1,
    ).length;
    const numberOfRemovedAnnotations = autogeneratedAnnotations.filter((it) => it.isRemoved).length;
    const numberOfUncheckedAnnotations = autogeneratedAnnotations.filter(
        (it) => !it.isRemoved && (it.reviewers ?? [])?.length === 0,
    ).length;

    const data = {
        labels: ['Correct', 'Changed', 'Removed', 'Unchecked'],
        datasets: [
            {
                data: [
                    numberOfCorrectAnnotations,
                    numberOfChangedAnnotations,
                    numberOfRemovedAnnotations,
                    numberOfUncheckedAnnotations,
                ],
                backgroundColor: [correctBg, changedBg, removedBg, uncheckedBg],
                borderColor: [correctBorder, changedBorder, removedBorder, uncheckedBorder],
                borderWidth: 1,
            },
        ],
    };

    const options = {
        plugins: {
            title: {
                display: true,
                text: annotationType,
                color: textColor,
            },
        },
    };

    return (
        <Box>
            <Pie data={data} options={options} />
        </Box>
    );
};
