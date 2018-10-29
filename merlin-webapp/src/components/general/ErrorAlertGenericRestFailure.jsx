import React from 'react';
import ErrorAlert from './ErrorAlert';

const ErrorAlertGenericRestFailure = (props) => <ErrorAlert
        titleKey={'common.alert.cantLoadData'}
        descriptionKey={'common.alert.genericRestAPIFailure'}
        action={{
            handleClick: props.handleClick,
            titleKey: 'common.alert.tryAgain'
        }}
    />

export default ErrorAlertGenericRestFailure;
