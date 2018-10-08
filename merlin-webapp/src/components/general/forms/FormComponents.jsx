import React from 'react';
import PropTypes from 'prop-types';

class FormLabelField extends React.Component {
    render() {
        return (
            <div className={'form-group row'}>
                <label className={`col-sm-${this.props.labelLength} col-form-label`}
                       htmlFor={this.props.htmlFor}>{this.props.label}</label>
                <div className={`col-sm-${this.props.fieldLength}`}>{this.props.field}</div>
            </div>
        );
    }
};
FormLabelField.propTypes = {
    label: PropTypes.node,
    field: PropTypes.node,
    htmlFor: PropTypes.string,
    labelLength: PropTypes.string,
    fieldLength: PropTypes.string
};
FormLabelField.defaultProps = {
    label: '',
    labelLength: '2',
    fieldLength: '10',
    htmlFor: ''
};

class FormFieldset extends React.Component {
    render() {
        return (
            <fieldset className="form-group" id={this.props.id}>
                <legend>{this.props.text}</legend>
                {this.props.content}
            </fieldset>
        );
    }
};
FormFieldset.propTypes = {
    text: PropTypes.string,
    id: PropTypes.string,
    content: PropTypes.node
};
FormFieldset.defaultProps = {
    id: ''
};

export {FormLabelField, FormFieldset};
