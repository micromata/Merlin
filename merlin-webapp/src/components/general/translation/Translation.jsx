import React from 'react';
import PropTypes from 'prop-types';
import {getTranslation} from '../../../utilities/i18n';

function Translation({name, children, params}) {
    return getTranslation(name, params) || children;
}

Translation.propTypes = {
    name: PropTypes.string.isRequired,
    children: PropTypes.node,
    params: PropTypes.arrayOf(PropTypes.node)
};

Translation.defaultProps = {
    children: '',
    params: []
};

export default Translation;
