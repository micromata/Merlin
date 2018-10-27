import React from 'react';
import PropTypes from 'prop-types';
import {FormFeedback, Input, UncontrolledTooltip} from 'reactstrap';
import {revisedRandId} from "../../../utilities/global";

class FormCheckbox extends React.Component {

    id = this.props.id || revisedRandId();

    render() {
        let tooltip = null;
        let hintId = null;
        if (this.props.hint) {
            hintId = `hint-${this.id}`;
            tooltip =
                <UncontrolledTooltip placement="right" target={hintId}>
                    {this.props.hint}
                </UncontrolledTooltip>;
        }
        let labelNode = <label
            className={'custom-control-label'}
            htmlFor={this.id}>
            {this.props.label}
        </label>;
        return (
            <React.Fragment>
                <div className="custom-control custom-checkbox" id={hintId}>
                    <input className="custom-control-input"
                           type="checkbox"
                           id={this.id}
                           name={this.props.name}
                           checked={this.props.checked}
                           onChange={this.props.onChange}
                    />
                    {labelNode}
                </div>
                {tooltip}
            </React.Fragment>
        );
    }
}

FormCheckbox.propTypes = {
    id: PropTypes.string,
    name: PropTypes.string,
    checked: PropTypes.bool,
    onChange: PropTypes.func,
    hint: PropTypes.string,
    label: PropTypes.node
};

FormCheckbox.defaultProps = {
    id: null,
    name: '',
    checked: false,
    onChange: null,
    hint: '',
    label: ''
};


export {
    FormCheckbox
};
