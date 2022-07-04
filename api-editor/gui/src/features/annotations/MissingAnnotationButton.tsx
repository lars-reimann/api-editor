import { IconButton, Tooltip } from '@chakra-ui/react';
import React from 'react';
import { FaFlag } from 'react-icons/fa';
import { missingAnnotationURL } from '../externalLinks/urlBuilder';
import { useAppSelector } from '../../app/hooks';
import { selectComplete, selectUsernameIsValid } from './annotationSlice';

interface MissingAnnotationButtonProps {
    target: string;
}

export const MissingAnnotationButton: React.FC<MissingAnnotationButtonProps> = function ({ target }) {
    const isComplete = Boolean(useAppSelector(selectComplete(target)));
    const isValidUsername = Boolean(useAppSelector(selectUsernameIsValid));
    const isDisabled = isComplete || !isValidUsername;

    return (
        <Tooltip label="Report a missing autogenerated annotation.">
            <IconButton
                icon={<FaFlag />}
                aria-label="Report Missing Annotation"
                size="sm"
                variant="outline"
                colorScheme="orange"
                disabled={isDisabled}
                onClick={() => {
                    window.open(missingAnnotationURL(target), '_blank');
                }}
            />
        </Tooltip>
    );
};