import { InferableAnnotation } from './InferableAnnotation';

export class AnnotatedPythonResult {
    constructor(
        readonly name: string,
        readonly type: string = 'Any',
        readonly typeInDocs: string = '',
        readonly description: string = '',
        readonly annotations: InferableAnnotation[] = [],
    ) {}
}
