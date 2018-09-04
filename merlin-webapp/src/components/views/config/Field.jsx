import React from 'react';
import {Button, FormControl, Glyphicon, InputGroup} from 'react-bootstrap';
import './style.css';

class ConfigField extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            editing: false,
            value: this.props.value,
        };

        this.handleClick = this.handleClick.bind(this);
        this.startEditing = this.startEditing.bind(this);
        this.cancelEditing = this.cancelEditing.bind(this);
        this.saveEdit = this.saveEdit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleKey = this.handleKey.bind(this);
    }

    componentDidMount() {
        document.addEventListener('mousedown', this.handleClick);
        document.addEventListener('keypress', this.handleKey);
    }

    componentWillUnmount() {
        document.removeEventListener('mousedown', this.handleClick);
        document.removeEventListener('keypress', this.handleKey);
    }

    handleClick(event) {
        if (this.ref && !this.ref.contains(event.target)) {
            this.cancelEditing();
        }
    }

    handleKey(event) {
        if (!this.state.editing) {
            return;
        }

        switch (event.key) {
            case "Escape":
                this.cancelEditing();
                break;
            case "Enter":
                this.saveEdit();
                break;
            default:
        }
    }

    startEditing() {
        if (this.state.editing) {
            return;
        }

        this.setState({
            editing: true,
            changed: this.state.value
        });
    }

    cancelEditing(event) {

        this.setState({
            editing: false
        });

        if (event !== undefined) {
            event.preventDefault();
        }
    }

    saveEdit(event) {
        let value = this.state.changed;

        if (this.props.type === 'number' && this.state.changed === '') {
            value = 0;
        }

        this.setState({
            value,
            editing: false
        });

        if (event !== undefined) {
            event.preventDefault();
        }
    }

    handleChange(event) {
        this.setState({
            changed: event.target.value
        });
    }

    render() {
        let value;

        if (this.state.editing) {
            value = (
                <InputGroup>
                    <FormControl
                        autoFocus
                        type={this.props.type}
                        value={this.state.changed}
                        onChange={this.handleChange}
                    />
                    <InputGroup.Button>
                        <Button onClick={this.saveEdit}>
                            <Glyphicon glyph="ok"/>
                        </Button>
                        <Button onClick={this.cancelEditing}>
                            <Glyphicon glyph="remove"/>
                        </Button>
                    </InputGroup.Button>
                </InputGroup>
            )
        } else {
            value = this.state.value;
        }

        return (
            <div className={`config-form`} ref={ref => this.ref = ref}>
                <span className={'config-label'}>{this.props.title}</span>
                <span className={'config-value'} onClick={this.startEditing}>{value}</span>
            </div>
        );
    }
}

export default ConfigField;
