import React from 'react';
import PropTypes from 'prop-types';
import styles from './LoadingOverlay.module.css'
import {IconSpinner} from '../IconComponents';

function LoadingOverlay({active}) {
    if (!active) {
        return <React.Fragment />;
    }

    return (
        <div className={styles.loadingOverlay}>
            <div className={styles.content}>
                <IconSpinner />
            </div>
        </div>
    );
}

LoadingOverlay.propTypes = {
    active: PropTypes.bool
};

LoadingOverlay.defaultProps = {
    active: false
};

export default LoadingOverlay;
