import { Box, Heading, SimpleGrid, VStack } from '@chakra-ui/react';
import React from 'react';
import { useAppSelector } from '../../app/hooks';
import {
    selectNumberOfAnnotationsChanged,
    selectNumberOfAnnotationsCreated,
    selectNumberOfAnnotationsDeleted,
    selectNumberOfAnnotationsMarkedAsCorrect,
    selectNumberOfCommentsTouched,
    selectNumberOfElementsMarkedAsComplete,
} from '../annotations/annotationSlice';
import { pluralize } from '../../common/util/stringOperations';
import {
    auditorAchievement,
    authorAchievement,
    cleanerAchievement,
    commentatorAchievement,
    completionistAchievement,
    editorAchievement,
} from './achievements';
import { AchievementCard } from './AchievementCard';

export const AchievementDisplay: React.FC = function () {
    const auditorCount = useAppSelector(selectNumberOfAnnotationsMarkedAsCorrect);
    const authorCount = useAppSelector(selectNumberOfAnnotationsCreated);
    const cleanerCount = useAppSelector(selectNumberOfAnnotationsDeleted);
    const completionistCount = useAppSelector(selectNumberOfElementsMarkedAsComplete);
    const commentatorCount = useAppSelector(selectNumberOfCommentsTouched);
    const editorCount = useAppSelector(selectNumberOfAnnotationsChanged);

    if (
        auditorCount === 0 &&
        authorCount === 0 &&
        cleanerCount === 0 &&
        completionistCount === 0 &&
        commentatorCount === 0 &&
        editorCount === 0
    ) {
        return null;
    }

    return (
        <VStack spacing={4}>
            <Heading as="h3" size="md">
                Achievements
            </Heading>
            <Box>
                <SimpleGrid columns={{ base: 1, wqhd: 2 }} spacing={4}>
                    <AchievementCard
                        currentCount={auditorCount}
                        achievement={auditorAchievement}
                        description={`${pluralize(auditorCount, 'annotation')} reviewed`}
                    />
                    <AchievementCard
                        currentCount={authorCount}
                        achievement={authorAchievement}
                        description={`${pluralize(authorCount, 'annotation')} created`}
                    />
                    <AchievementCard
                        currentCount={cleanerCount}
                        achievement={cleanerAchievement}
                        description={`${pluralize(cleanerCount, 'annotation')} removed`}
                    />
                    <AchievementCard
                        currentCount={completionistCount}
                        achievement={completionistAchievement}
                        description={`${pluralize(completionistCount, 'API element')} marked as complete`}
                    />
                    <AchievementCard
                        currentCount={commentatorCount}
                        achievement={commentatorAchievement}
                        description={`${pluralize(commentatorCount, 'comment')} touched`}
                    />
                    <AchievementCard
                        currentCount={editorCount}
                        achievement={editorAchievement}
                        description={`${pluralize(editorCount, 'annotation')} changed`}
                    />
                </SimpleGrid>
            </Box>
        </VStack>
    );
};
