import React from 'react';
import {connect} from 'react-redux';
import {Button, FormControl, Glyphicon, InputGroup} from 'react-bootstrap';
import {setConfigProperty} from '../../../actions';

class EditingField extends React.Component {

    state = {
        value: this.props.value
    };

    constructor(props) {
        super(props);

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleOkButtonClick = this.handleOkButtonClick.bind(this);
        this.handleRemoveButtonClick = this.handleRemoveButtonClick.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);
        this.handleMouseDown = this.handleMouseDown.bind(this);
        this.cancelEditing = this.cancelEditing.bind(this);
        this.saveEdit = this.saveEdit.bind(this);
    }

    componentDidMount() {
        document.addEventListener('mousedown', this.handleMouseDown);
        document.addEventListener('keypress', this.handleKeyPress);
    }

    componentWillUnmount() {
        document.removeEventListener('mousedown', this.handleMouseDown)
        document.removeEventListener('keypress', this.handleKeyPress)
    }

    handleKeyPress(event) {
        switch (event.key) {
            case 'Escape':
                this.cancelEditing();
                break;
            case 'Enter':
                this.saveEdit();
                break;
            default:
        }
    }

    handleMouseDown(event) {
        if (this.ref && !this.ref.contains(event.target)) {
            this.cancelEditing();
        }
    }

    handleOkButtonClick(event) {
        event.preventDefault();

        this.saveEdit();
    }

    handleRemoveButtonClick(event) {
        event.preventDefault();

        this.cancelEditing();
    }

    handleInputChange(event) {
        this.setState({
            value: event.target.value
        });
    }

    cancelEditing() {
        this.props.stopEditing();
    }

    saveEdit() {
        this.props.setProperty(this.props.name, this.state.value);

        this.props.stopEditing();
    }

    render() {

        return (
            <div
                ref={ref => this.ref = ref}
            >
                <InputGroup>
                    <FormControl
                        autoFocus
                        type={this.props.type}
                        value={this.state.value}
                        onChange={this.handleInputChange}
                    />
                    <InputGroup.Button>
                        <Button
                            onClick={this.handleOkButtonClick}
                        >
                            <Glyphicon glyph={'ok'}/>
                        </Button>
                        <Button
                            onClick={this.handleRemoveButtonClick}
                        >
                            <Glyphicon glyph={'remove'}/>
                        </Button>
                    </InputGroup.Button>
                </InputGroup>
            </div>
        );
    }

}

const mapStateToProps = () => ({});

const actions = {
    setProperty: setConfigProperty
};

export default connect(mapStateToProps, actions)(EditingField);
