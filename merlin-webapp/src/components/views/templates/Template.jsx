import React from 'react';
import {Modal} from 'react-bootstrap';
import './style.css';

class Template extends React.Component {

    state = {
        showRunModal: false
    };

    showRunModal = () => {
        this.setState({
            showRunModal: true
        });
    };

    closeRunModal = () => {
        this.setState({
            showRunModal: false
        });
    };

    render = () => <div
        className={'template-container'}
    >
        <div
            className={'template'}
            onClick={this.showRunModal}
        >
            <span className={'template-name'}>{this.props.name}</span>
            <span className={'template-description'}>{this.props.description}</span>
            <span className={'hint'}>Click to run.</span>
        </div>

        <Modal
            show={this.state.showRunModal}
            onHide={this.closeRunModal}
        >
            <Modal.Header>
                <Modal.Title>
                    Run {this.props.name}
                </Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <h4>Modal Content follows here</h4>
            </Modal.Body>
        </Modal>
    </div>;

    constructor(props) {
        super(props);

        this.showRunModal = this.showRunModal.bind(this);
        this.closeRunModal = this.closeRunModal.bind(this);
    }
}

export default Template;