import PropTypes from 'prop-types';
import {getTranslation} from '../../../utilities/i18n';
import {isDevelopmentMode} from '../../../utilities/global';

function I18n({name, children, params}) {
    return getTranslation(name, params) || (isDevelopmentMode() ? `??? ${children ? children : name} ???` : children);
}

I18n.propTypes = {
    name: PropTypes.string.isRequired,
    children: PropTypes.node,
    params: PropTypes.arrayOf(PropTypes.node)
};

I18n.defaultProps = {
    children: '',
    params: []
};

export default I18n;
