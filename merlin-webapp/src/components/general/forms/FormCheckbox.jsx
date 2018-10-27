import React from 'react';
import PropTypes from 'prop-types';
import {UncontrolledTooltip} from 'reactstrap';
import {revisedRandId} from "../../../utilities/global";
import classNames from 'classnames';

class FormCheckbox extends React.Component {

    _id = this.props.id || revisedRandId();

    render() {
        let tooltip = null;
        let hintId = null;
        if (this.props.hint) {
            hintId = `hint-${this._id}`;
            tooltip =
                <UncontrolledTooltip placement="right" target={hintId}>
                    {this.props.hint}
                </UncontrolledTooltip>;
        }
        let labelNode = <label
            className={'custom-control-label'}
            htmlFor={this._id}>
            {this.props.label}
        </label>;
        const {id, className, ...other} = this.props;
        return (
            <React.Fragment>
                <div className="custom-control custom-checkbox" id={hintId}>
                    <input type="checkbox"
                           id={this._id}
                           className={classNames('custom-control-input', className)}
                           {...other}
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
    checked: false,
    onChange: null
};


export {
    FormCheckbox
};
