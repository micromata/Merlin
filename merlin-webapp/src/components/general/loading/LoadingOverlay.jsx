import React from 'react';
import styles from './LoadingOverlay.module.css'
import {IconSpinner} from '../IconComponents';

function LoadingOverlay() {
    return (
        <div className={styles.loadingOverlay}>
            <div className={styles.content}>
                <IconSpinner />
            </div>
        </div>
    );
}

export default LoadingOverlay;
