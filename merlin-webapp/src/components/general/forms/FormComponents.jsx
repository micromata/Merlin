import React from 'react';
import PropTypes from 'prop-types';


class FormLabelField extends React.Component {
    render() {
        return (
            <div className={'form-group row'}>
                <label className={`col-sm-${this.props.labelLength} col-form-label`}
                       htmlFor={this.props.name}>{this.props.label}</label>
                <div className={`col-sm-${this.props.fieldLength}`}>{this.props.field}</div>
            </div>
        );
    }
};

FormLabelField.propTypes = {
    label: PropTypes.node,
    field: PropTypes.node,
    name: PropTypes.node,
    labelLength: PropTypes.string,
    fieldLength: PropTypes.string
    //onClick: PropTypes.func
};

FormLabelField.defaultProps = {
    labelLength: '2',
    fieldLength: '10'
    //  onClick: function () {
    //      alert("Hello");
    //  }
};

export default FormLabelField;