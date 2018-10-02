import React from 'react';
import {Button, ControlLabel, FormControl, FormGroup, HelpBlock, Modal} from 'react-bootstrap';
import './style.css';
import {getRestServiceUrl} from "../../../actions/global";
import downloadFile from "../../../utilities/download";
import DropArea from "../../general/droparea/Component";

class Template extends React.Component {

    path = getRestServiceUrl('templates');
    state = {
        showRunModal: false,
        runConfiguration: this.props.variableDefinitions.reduce((accumulator, current) => ({
            ...accumulator,
            [current.name]: current.allowedValuesList && current.allowedValuesList.length !== 0 ?
                current.allowedValuesList[0] : ''
        }), {})
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

    handleRunConfigurationChange = (name) => (event) => {
        this.setState({
            runConfiguration: {
                ...this.state.runConfiguration,
                [name]: event.target.value
            }
        });
    };

    runTemplate = () => {
        fetch(`${this.path}/run`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                templateCanonicalPath: this.props.fileDescriptor.canonicalPath,
                variables: this.state.runConfiguration
            })
        })
            .then(response => response.blob())
            .then(blob => downloadFile(blob, this.props.fileDescriptor.filename));
    };

    runSerialTemplate = (file) => {
        // TODO CALL REST URL
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
                <DropArea
                    upload={this.runSerialTemplate}
                />
                <br/>
                <form>
                    {this.props.variableDefinitions.map(item => {
                        let formControl;
                        const formControlProps = {
                            value: this.state.runConfiguration[item.name],
                            onChange: this.handleRunConfigurationChange(item.name)
                        };

                        if (item.allowedValuesList && item.allowedValuesList.length !== 0) {
                            formControl = <FormControl
                                {...formControlProps}
                                componentClass={'select'}
                            >
                                {item.allowedValuesList.map(option => <option
                                    key={`template-run-variable-${item.refId}-select-${option}`}
                                    value={option}
                                >
                                    {option}
                                </option>)}
                            </FormControl>
                        } else {
                            formControl = <FormControl
                                {...formControlProps}
                                type={item.type}
                            />;
                        }

                        return <FormGroup
                            key={`template-run-variable-${item.refId}`}
                        >
                            <ControlLabel>
                                {item.name}
                            </ControlLabel>
                            {formControl}
                            {item.description ?
                                <HelpBlock>{item.description}</HelpBlock> :
                                undefined
                            }
                        </FormGroup>;
                    })}
                </form>
                <Button
                    bsStyle={'success'}
                    onClick={this.runTemplate}
                >
                    Run
                </Button>
            </Modal.Body>
        </Modal>
    </div>;

    constructor(props) {
        super(props);

        this.showRunModal = this.showRunModal.bind(this);
        this.closeRunModal = this.closeRunModal.bind(this);
        this.handleRunConfigurationChange = this.handleRunConfigurationChange.bind(this);
        this.runTemplate = this.runTemplate.bind(this);
        this.runSerialTemplate = this.runSerialTemplate.bind(this);
    }
}

export default Template;