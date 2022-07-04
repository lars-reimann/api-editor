import {
    FormControl,
    FormErrorIcon,
    FormErrorMessage,
    FormLabel,
    HStack,
    IconButton,
    Input,
    Text,
    Textarea,
} from '@chakra-ui/react';
import React, { useEffect } from 'react';
import { useFieldArray, useForm } from 'react-hook-form';
import { FaPlus, FaTrash } from 'react-icons/fa';
import { useAppDispatch, useAppSelector } from '../../../app/hooks';
import { pythonIdentifierPattern } from '../../../common/validation';
import { PythonDeclaration } from '../../packageData/model/PythonDeclaration';
import { selectEnumAnnotation, upsertEnumAnnotation } from '../annotationSlice';
import { AnnotationForm } from './AnnotationForm';
import { hideAnnotationForm } from '../../ui/uiSlice';

interface EnumFormProps {
    target: PythonDeclaration;
}

interface EnumFormState {
    enumName: string;
    pairs: {
        stringValue: string;
        instanceName: string;
    }[];
    comment: string;
}

export const EnumForm: React.FC<EnumFormProps> = function ({ target }) {
    const targetPath = target.id;

    // Hooks -----------------------------------------------------------------------------------------------------------
    const previousAnnotation = useAppSelector(selectEnumAnnotation(target.id));
    const dispatch = useAppDispatch();

    const {
        control,
        register,
        handleSubmit,
        setFocus,
        reset,
        formState: { errors },
    } = useForm<EnumFormState>({
        defaultValues: {
            enumName: '',
            pairs: [
                {
                    stringValue: '',
                    instanceName: '',
                },
            ],
            comment: '',
        },
    });
    const { fields, append, remove } = useFieldArray({
        control,
        name: 'pairs',
    });

    useEffect(() => {
        try {
            setFocus('enumName');
        } catch (e) {
            // ignore
        }
    }, [setFocus]);

    useEffect(() => {
        reset({
            enumName: previousAnnotation?.enumName ?? '',
            pairs: previousAnnotation?.pairs ?? [
                {
                    stringValue: '',
                    instanceName: '',
                },
            ],
            comment: previousAnnotation?.comment ?? '',
        });
    }, [reset, previousAnnotation, targetPath]);

    // Event handlers --------------------------------------------------------------------------------------------------

    const onRemove = (index: number) => () => {
        if (fields.length > 1) {
            remove(index);
        }
    };

    const onAppend = () => {
        append({
            stringValue: '',
            instanceName: '',
        });
    };

    const onSave = (data: EnumFormState) => {
        dispatch(
            upsertEnumAnnotation({
                target: targetPath,
                ...data,
            }),
        );
        dispatch(hideAnnotationForm());
    };

    const onCancel = () => {
        dispatch(hideAnnotationForm());
    };

    // Rendering -------------------------------------------------------------------------------------------------------

    return (
        <AnnotationForm
            heading={`${previousAnnotation ? 'Edit' : 'Add'} @enum Annotation`}
            description="Replace this string parameter with an enum parameter."
            onSave={handleSubmit(onSave)}
            onCancel={onCancel}
        >
            {/* Enum name -------------------------------------------------------------------------------------------*/}
            <FormControl isInvalid={Boolean(errors.enumName)}>
                <FormLabel>Enum name for &quot;{target.name}&quot;:</FormLabel>
                <Input
                    {...register('enumName', {
                        required: 'This is required.',
                        pattern: pythonIdentifierPattern,
                    })}
                />
                <FormErrorMessage>
                    <FormErrorIcon /> {errors.enumName?.message}
                </FormErrorMessage>
            </FormControl>

            {/* Enum pairs ------------------------------------------------------------------------------------------*/}
            <HStack>
                <Text fontSize="md" fontWeight="medium" w="100%">
                    String value:
                </Text>
                <Text fontSize="md" fontWeight="medium" w="100%">
                    Instance name:
                </Text>
                <IconButton icon={<FaPlus />} aria-label="Add enum pair" colorScheme="green" onClick={onAppend} />
            </HStack>

            {fields.map((field, index) => (
                <HStack key={field.id} alignItems="flex-start">
                    <FormControl isInvalid={Boolean(errors?.pairs?.[index]?.stringValue)}>
                        <Input
                            {...register(`pairs.${index}.stringValue`, {
                                required: 'This is required.',
                            })}
                        />
                        <FormErrorMessage>
                            <FormErrorIcon /> {errors?.pairs?.[index]?.stringValue?.message}
                        </FormErrorMessage>
                    </FormControl>

                    <FormControl isInvalid={Boolean(errors?.pairs?.[index]?.instanceName)}>
                        <Input
                            {...register(`pairs.${index}.instanceName`, {
                                required: 'This is required.',
                                pattern: pythonIdentifierPattern,
                            })}
                        />
                        <FormErrorMessage>
                            <FormErrorIcon /> {errors?.pairs?.[index]?.instanceName?.message}
                        </FormErrorMessage>
                    </FormControl>

                    <IconButton
                        icon={<FaTrash />}
                        aria-label="Delete enum pair"
                        colorScheme="red"
                        disabled={fields.length <= 1}
                        onClick={onRemove(index)}
                    />
                </HStack>
            ))}

            <FormControl>
                <FormLabel>Comment:</FormLabel>
                <Textarea {...register('comment')} />
            </FormControl>
        </AnnotationForm>
    );
};
