import React from 'react';
import './EditableTextField.css';
import {Button, Input, InputGroup, InputGroupAddon} from 'reactstrap';
import {IconCheck, IconCancel} from '../IconComponents'

class EditableTextField extends React.Component {

    state = {
        editing: false
    };

    constructor(props) {
        super(props);

        this.startEditing = this.startEditing.bind(this);
        this.stopEditing = this.stopEditing.bind(this);
    }

    startEditing = () => {
        this.setState({
            editing: true
        });
    };

    stopEditing = value => {
        this.setState({
            editing: false
        });

        if (value && this.props.onChange) {
            this.props.onChange(value);
        }
    };

    render = () => <div className={'editable-text-field'}>
        <div className={'editable-text-div editable-text-field-label'}>{this.props.title}</div>
        <div className={'editable-text-div editable-text-field-value-container'}>
            {this.state.editing ?
                <EditableTextFieldInput
                    type={this.props.type}
                    value={this.props.value}
                    name={this.props.title}
                    stopEditing={this.stopEditing}
                /> :
                <div className={'text-truncate editable-text-field-value'} onClick={this.startEditing}
                     title={this.props.value}>
                    {this.props.value}
                </div>
            }
        </div>
    </div>;
}

class EditableTextFieldInput extends React.Component {

    state = {
        value: this.props.value
    };

    constructor(props) {
        super(props);

        this.setReference = this.setReference.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.stopEditing = this.stopEditing.bind(this);
        this.handleMouseDown = this.handleMouseDown.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
    }

    componentDidMount = () => {
        document.addEventListener('mousedown', this.handleMouseDown);
        document.addEventListener('keypress', this.handleKeyPress);
    };

    componentWillUnmount = () => {
        document.removeEventListener('mousedown', this.handleMouseDown);
        document.removeEventListener('keypress', this.handleKeyPress);
    };

    setReference = reference => this.reference = reference;

    handleInputChange = event => this.setState({
        value: event.target.value
    });

    handleMouseDown = event =>
        this.reference && !this.reference.contains(event.target) ?
            this.stopEditing(false)() : undefined;

    handleKeyPress = event => {
        switch (event.key) {
            case 'Escape':
                this.stopEditing(false)();
                return;
            case 'Enter':
                if (this.props.type !== 'textarea') {
                    this.stopEditing(true)();
                    return;
                }
                break;
            default:
        }
    };

    stopEditing = (save = false) => () =>
        this.props.stopEditing(save ? this.state.value : undefined);

    render = () => <div ref={this.setReference}>
        <InputGroup>
            <Input
                autoFocus
                type={this.props.type}
                value={this.state.value}
                onChange={this.handleInputChange}/>
            <InputGroupAddon addonType={'append'}>
                <Button
                    onClick={this.stopEditing(true)}
                    color={'success'}
                >
                    <IconCheck/>
                </Button>
                <Button
                    onClick={this.stopEditing(false)}
                    color={'danger'}
                >
                    <IconCancel/>
                </Button>
            </InputGroupAddon>
        </InputGroup>
    </div>;
}


export default EditableTextField;
