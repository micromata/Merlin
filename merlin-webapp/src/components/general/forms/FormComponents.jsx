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
    htmlFor: PropTypes.node,
    labelLength: PropTypes.string,
    fieldLength: PropTypes.string
    //onClick: PropTypes.func
};

FormLabelField.defaultProps = {
    label: '',
    labelLength: '2',
    fieldLength: '10',
    htmlFor: ''
    //  onClick: function () {
    //      alert("Hello");
    //  }
};

export default FormLabelField;