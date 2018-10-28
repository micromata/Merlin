import React from 'react';
import PropTypes from 'prop-types';
import {UncontrolledTooltip} from 'reactstrap';
import classNames from 'classnames';
import {revisedRandId} from "../../../utilities/global";
import I18n from "../translation/I18n";

class FormButton extends React.Component {

    _id = this.props.id || this.props.name || revisedRandId();

    render() {
        let tooltip = null;
        const {className, hint, hintKey, hintPlacement, id, bsStyle, ...other} = this.props;
        if (hint || hintKey) {
            tooltip = <UncontrolledTooltip placement={hintPlacement} target={this._id}>
                {hintKey ? <I18n name={hintKey}/> : hint}
            </UncontrolledTooltip>;
        }
        return (
            <React.Fragment>
                <button
                    {...other}
                    id={this._id}
                    className={classNames(`btn btn-${bsStyle}`, className)}
                >
                    {this.props.children}
                </button>
                {tooltip}
            </React.Fragment>
        );
    }
};

FormButton.propTypes = {
    id: PropTypes.string,
    bsStyle: PropTypes.oneOf(['primary', 'outline-primary', null]),
    type: PropTypes.string,
    onClick: PropTypes.func,
    hint: PropTypes.string,
    hintKey: PropTypes.string,
    hintPlacement: PropTypes.oneOf(['right', 'top']),
    disabled: PropTypes.bool,
    children: PropTypes.node
};
FormButton.defaultProps = {
    bsStyle: 'outline-primary',
    type: 'button',
    hintPlacement: 'top',
    disabled: false
};

export {
    FormButton
};
