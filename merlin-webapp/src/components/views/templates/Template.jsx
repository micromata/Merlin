import React from 'react';
import {ControlLabel, FormControl, FormGroup, HelpBlock, Modal} from 'react-bootstrap';
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
                <form>
                    {this.props.variableDefinitions.map(item => {
                        let formControl;

                        console.log(item);

                        if (item.allowedValuesList && item.allowedValuesList.length !== 0) {
                            formControl = <FormControl
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