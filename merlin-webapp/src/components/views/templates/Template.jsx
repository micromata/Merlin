import React from 'react';
import {Glyphicon, Modal} from 'react-bootstrap';
import './templateStyle.css';
import ConfigurationField from '../../general/configuration/Field';
import ConfigurationFieldLabel from '../../general/configuration/FieldLabel';

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

        <Modal
            show={this.state.editing}
            onHide={this.closeEditingModal}
            className={'template-configuration-modal'}
        >
            <Modal.Header closeButton>
                <Modal.Title>{this.props.name}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {/* TODO ADD updateValue Method */}
                <ConfigurationField
                    title={'Name'}
                    value={this.props.name}
                    type={'text'}
                />
                <ConfigurationField
                    title={'Description'}
                    value={this.props.description}
                    type={'text'}
                />
                <ConfigurationField
                    title={'File Name Pattern'}
                    value={this.props.filenamePattern}
                    type={'text'}
                />

                <ConfigurationFieldLabel title={'Variables:'}/>
                {this.props.variableDefinitions.map(item => <div
                    key={item.id}
                    className={'variable'}
                >
                    <ConfigurationField
                        title={'Name'}
                        value={item.name}
                        type={'text'}
                    />
                    <ConfigurationField
                        title={'Options'}
                        value={{
                            'Required': item.required,
                            'Unique': item.unique
                        }}
                        type={'checkbox-group'}
                    />
                    <ConfigurationField
                        title={'Allowed Values'}
                        value={item.allowedValuesList}
                        type={'list'}
                    />
                </div>)}

            </Modal.Body>
        </Modal>
    </div>;
}

export default Template;
