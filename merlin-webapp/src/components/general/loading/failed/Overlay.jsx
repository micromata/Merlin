import React from 'react';
import {Modal, ModalBody, ModalHeader} from 'reactstrap';

function FailedOverlay({title, text, closeModal, active}) {
    return (
        <Modal isOpen={active} toggle={closeModal}>
            <ModalHeader toggle={closeModal}>{title || 'Fetch failed'}</ModalHeader>
            <ModalBody>
                {text}
            </ModalBody>
        </Modal>
    );
}

export default FailedOverlay;
