import PropTypes from 'prop-types';
import {getTranslation} from '../../../utilities/i18n';
import {isDevelopmentMode} from '../../../utilities/global';

function Translation({name, children, params}) {
    return getTranslation(name, params) || (isDevelopmentMode() ? `??? ${children} ???` : children);
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
