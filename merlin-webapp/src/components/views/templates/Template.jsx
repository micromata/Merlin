import React from 'react';
import {Button, ControlLabel, FormControl, FormGroup, Glyphicon, Modal, Tab, Tabs} from 'react-bootstrap';
import './templateStyle.css';

class Template extends React.Component {

    // TODO ADD DIRECT URL SUPPORT
    state = {
        editing: false,
        run: false,
        runTab: 1,
        runConfiguration: this.props.variableDefinitions.reduce((accumulator, current) => ({
            ...accumulator,
            [current.name]: current.allowedValuesList.length === 0 ? '' : current.allowedValuesList[0]
        }), {})
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
            variables: this.state.runConfiguration
        });
    };

    handleRunVariableChange = (name) => (event) => {
        this.setState({
            runConfiguration: {
                ...this.state.runConfiguration,
                [name]: event.target.value
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
                        <form>
                            {this.props.variableDefinitions.map(item => {

                                const controlProps = {
                                    id: `run-template-variable-${item.refId}`,
                                    placeholder: item.name,
                                    value: this.state.runConfiguration[item.name],
                                    onChange: this.handleRunVariableChange(item.name)
                                };

                                return <FormGroup
                                    key={`run-${item.refId}`}
                                >
                                    <ControlLabel>{item.name}</ControlLabel>
                                    {item.allowedValuesList.length === 0 ?
                                        <FormControl
                                            type={'text'}
                                            {...controlProps}
                                        /> :
                                        <FormControl
                                            componentClass={'select'}
                                            {...controlProps}
                                        >
                                            {item.allowedValuesList.map(value => <option
                                                key={`run-template-variable-option-${value}`}
                                                value={value}
                                            >
                                                {value}
                                            </option>)}
                                        </FormControl>
                                    }
                                </FormGroup>;
                            })}
                        </form>
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
        this.handleRunVariableChange = this.handleRunVariableChange.bind(this);
    }
}

export default Template;
