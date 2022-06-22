import React from 'react';
import { Box, Heading, HStack } from '@chakra-ui/react';
import { useAppSelector } from '../../app/hooks';
import { selectMatchedNodes } from '../packageData/apiSlice';
import { selectAllAnnotationsOnTargets, selectAnnotationStore } from '../annotations/annotationSlice';
import { Pie } from 'react-chartjs-2';

import { ArcElement, Chart as ChartJS, Title, Tooltip } from 'chart.js';

ChartJS.register(ArcElement, Title, Tooltip);

export const ProgressStatistics = function () {
    // Completion Progress
    const completed = useAppSelector(selectAnnotationStore).completes;
    const matchedNodes = useAppSelector(selectMatchedNodes);
    const numberOfMatchedNodes = matchedNodes.length;
    const numberOfCompleteMatchedNodes = matchedNodes.filter((it) => it.id in completed).length;

    const completionData = {
        labels: ['Complete', 'Incomplete?'],
        datasets: [
            {
                data: [numberOfCompleteMatchedNodes, numberOfMatchedNodes - numberOfCompleteMatchedNodes],
                backgroundColor: ['rgba(164,255,99,0.2)', 'rgba(162,162,162,0.2)'],
                borderColor: ['rgba(92,154,45,0.2)', 'rgba(115,115,115,0.2)'],
                borderWidth: 1,
            },
        ],
    };

    const completionOptions = {
        plugins: {
            title: {
                display: true,
                text: 'Completion Progress',
            },
        },
    };

    // Review Progress
    const allAnnotations = useAppSelector(selectAllAnnotationsOnTargets(matchedNodes.map((it) => it.id)));
    const numberOfAnnotations = allAnnotations.length;
    const numberOfReviewedAnnotations = allAnnotations.filter((it) => (it.reviewers?.length ?? 0) > 0).length;

    const correctnessData = {
        labels: ['Correct', 'Incorrect?'],
        datasets: [
            {
                data: [numberOfReviewedAnnotations, numberOfAnnotations - numberOfReviewedAnnotations],
                backgroundColor: ['rgba(164,255,99,0.2)', 'rgba(162,162,162,0.2)'],
                borderColor: ['rgba(92,154,45,0.2)', 'rgba(115,115,115,0.2)'],
                borderWidth: 1,
            },
        ],
    };

    const correctnessOptions = {
        plugins: {
            title: {
                display: true,
                text: 'Review Progress',
            },
        },
    };

    return (
        <>
            <Heading as="h3" size="md">
                Progress
            </Heading>
            <Box width="100%">
                <HStack width="100%" justify="space-between">
                    <Box width="40%">
                        <Pie data={completionData} options={completionOptions} />
                    </Box>
                    <Box width="40%">
                        <Pie data={correctnessData} options={correctnessOptions} />
                    </Box>
                </HStack>
            </Box>
        </>
    );
};