import React, {FormEvent, useState} from "react";
import "../ParameterView.css";
import {Button, Container, Form, Modal, Col, Row} from "react-bootstrap";
import {Formik} from 'formik';
import {nameValidation} from "../../../util/validation";
import EnumPair from "../../../model/EnumPair";
import EnumPairRow from "./EnumPairRow";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";

type showDialogState = {
    dialogState: boolean, setDialogState: Setter<boolean>, currentName: string,
    setCurrentName: Setter<string>, enumList: EnumPair[], setEnumList: Setter<EnumPair[]>
}

export default function EnumDialog({
                                       dialogState,
                                       setDialogState,
                                       currentName,
                                       setCurrentName,
                                       setEnumList,
                                       enumList
                                   }: showDialogState): JSX.Element {

    const [nameValid, setNameValid] = useState(true);
    const [currentRenameValue, setCurrentRenameValue] = useState("");

    const onInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentRenameValue(event.target.value);
        setNameValid(nameValidation(event.target.value));
    };

    const pair1 = new EnumPair("hello", "world1");
    const pair2 = new EnumPair("hello", "world2");
    const pair3 = new EnumPair("hello", "world3");
    const listOfEnumPairs = [pair1, pair2, pair3];
    const listItems = listOfEnumPairs.map((pair, index) =>
        <EnumPairRow pair={pair} key={String(index)}/>);


    const resetData = () => {
        setDialogState(false);
        setCurrentRenameValue("");
        setNameValid(true);
    };

    const onFormSubmit = (e: FormEvent) => {
        e.preventDefault();
        if (currentRenameValue && currentRenameValue != currentName && nameValid) {
            currentName = currentRenameValue;
            setCurrentName(currentRenameValue);
            resetData();
        }
    };

    const handleClose = () => {
        resetData();
    };

    return (
        <Modal
            show={dialogState}
            onHide={handleClose}
            size="lg"
        >
            <Modal.Header closeButton>
                <Modal.Title>Add @enum Annotation</Modal.Title>
            </Modal.Header>

            <Formik
                onSubmit={console.log}
                initialValues={{
                    currentEnumNameValue: ''
                }}
            >
                {() => (

                    <Form noValidate>
                        <Modal.Body>
                            <Form.Group>
                                <Form.Label>
                                    New Name:
                                </Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder={currentName}
                                    value={currentRenameValue}
                                    onChange={onInput}
                                    isInvalid={!nameValid}
                                />
                                <Container>
                                    <Row className="enum-pair-row">
                                        <Col xs={5}>String/Value:</Col>
                                        <Col xs={5}>Name of enum:</Col>
                                        <Col xs={2}><FontAwesomeIcon icon={faPlus}/></Col>
                                    </Row>
                                    {listItems}
                                </Container>
                            </Form.Group>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={handleClose}>
                                Close
                            </Button>
                            <Button variant="primary" type="submit" onClick={onFormSubmit}>
                                Submit
                            </Button>
                        </Modal.Footer>
                    </Form>
                )}
            </Formik>
        </Modal>
    );
}
