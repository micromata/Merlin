import React from 'react';
import {Glyphicon, Modal} from 'react-bootstrap';
import './templateStyle.css';
import ConfigurationField from '../../general/configuration/Field';

class Template extends React.Component {

    // TODO ADD DIRECT URL SUPPORT
    state = {
        editing: false
    };

    constructor(props) {
        super(props);

        this.openEditingModal = this.openEditingModal.bind(this);
        this.closeEditingModal = this.closeEditingModal.bind(this);
    }

    openEditingModal = () => {
        this.setState({
            editing: true
        });
    };

    closeEditingModal = () => {
        this.setState({
            editing: false
        });
    };

    render = () => <div className={'template-container'}>
        <div className={'template'} onClick={this.openEditingModal}>
            <span className={'name'}>{this.props.name}</span>
            <span className={'description'}>{this.props.description}</span>
            <div className={'icons'}>
                <span className={'play'}>
                    <Glyphicon glyph={'play'}/>
                </span>
            </div>
        </div>

        <Modal show={this.state.editing} onHide={this.closeEditingModal}>
            <Modal.Header closeButton>
                <Modal.Title>{this.props.name}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {/* TODO ADD updateValue Method */}
                <ConfigurationField
                    title={'Name'}
                    value={this.props.name}
                />
                <ConfigurationField
                    title={'Description'}
                    value={this.props.description}
                />
            </Modal.Body>
        </Modal>
    </div>;
}

export default Template;
