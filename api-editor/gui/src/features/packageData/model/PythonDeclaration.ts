import { Optional } from '../../../common/util/types';

export abstract class PythonDeclaration {
    abstract readonly id: string;
    abstract readonly name: string;
    abstract readonly isPublic: boolean;

    abstract parent(): Optional<PythonDeclaration>;

    abstract children(): PythonDeclaration[];

    abstract preferredQualifiedName(): string;

    getUniqueName(): string {
        return this.name;
    }

    root(): PythonDeclaration {
        let current: PythonDeclaration = this;
        while (true) {
            const parent = current.parent();
            if (!parent) {
                return current;
            }
            current = parent;
        }
    }

    *ancestorsOrSelf(): Generator<PythonDeclaration> {
        let current: Optional<PythonDeclaration> = this;
        while (current) {
            yield current;
            current = current.parent();
        }
    }

    *descendantsOrSelf(): Generator<PythonDeclaration> {
        yield this;
        for (const child of this.children()) {
            yield* child.descendantsOrSelf();
        }
    }
}
