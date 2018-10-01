import React from 'react';
import {Button, Glyphicon, Modal, Tab, Tabs} from 'react-bootstrap';
import './templateStyle.css';
import ConfigurationField from '../../general/configuration/Field';
import ConfigurationFieldLabel from '../../general/configuration/FieldLabel';

class Template extends React.Component {

    // TODO ADD DIRECT URL SUPPORT
    state = {
        editing: false,
        run: false,
        runTab: 1
    };

    handleRunTabSelect = key => {
        this.setState({
            runTab: key
        });
    };

    openEditingModal = () => {
        this.setState({
            // TODO CHANGE BACK TO EDITING WHEN EDITING PANE IS NEEDED
            run: true
        });
    };

    closeEditingModal = () => {
        this.setState({
            editing: false
        });
    };
    openRunModal = (event) => {
        if (event) {
            event.stopPropagation();
        }

        this.setState({
            run: true
        });
    };
    closeRunModal = () => {
        this.setState({
            run: false
        });
    };

    runTemplate = () => {
        this.props.runTemplate({
            templateDefinitionId: this.props.id,
            templateId: this.props.name,
            variables: {
                Employee: 'Berta Smith',
                WeeklyHours: 40,
                Gender: 'female',
                NumberOfLeaveDays: 30,
                Data: 1538303576721
            }
        });
    };

    render = () => <div className={'template-container'}>
        <div className={'template'} onClick={this.openEditingModal}>
            <span className={'name'}>{this.props.name}</span>
            <span className={'description'}>{this.props.description}</span>
            <div className={'icons'}>
                <span
                    className={'play'}
                    onClick={this.openRunModal}
                >
                    <Glyphicon glyph={'play'}/>
                </span>
            </div>
        </div>

        <Modal
            show={this.state.editing}
            onHide={this.closeEditingModal}
            className={'template-configuration-modal'}
            bsSize={'large'}
        >
            <Modal.Header closeButton>
                <Modal.Title>{this.props.name} - Edit</Modal.Title>
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

        <Modal
            show={this.state.run}
            onHide={this.closeRunModal}
            bsSize={'large'}
        >
            <Modal.Header closeButton>
                <Modal.Title>{this.props.name} - Run</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Tabs
                    activeKey={this.state.runTab}
                    onSelect={this.handleRunTabSelect}
                    id={'run-tabs'}
                >
                    <Tab
                        eventKey={1}
                        title={'Single'}
                        // TODO SINGLE CONFIGURATION
                    >
                    </Tab>
                    <Tab
                        eventKey={2}
                        title={'Bulk'}
                        disabled
                        // TODO BULK CONFIGURATION
                    />
                </Tabs>
                <Button onClick={this.runTemplate}>Run</Button>
            </Modal.Body>
        </Modal>
    </div>;

    constructor(props) {
        super(props);

        this.openEditingModal = this.openEditingModal.bind(this);
        this.closeEditingModal = this.closeEditingModal.bind(this);
        this.openRunModal = this.openRunModal.bind(this);
        this.closeRunModal = this.closeRunModal.bind(this);
        this.handleRunTabSelect = this.handleRunTabSelect.bind(this);
        this.runTemplate = this.runTemplate.bind(this);
    }
}

export default Template;
