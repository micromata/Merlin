import React from 'react';
import PropTypes from 'prop-types';

function PageHeader({children}) {
    return (
        <div className={'pb-2 mt-4 mb-2 border-bottom pageheader'}>
            {children}
        </div>
    );
}

PageHeader.propTypes = {
    children: PropTypes.node
};

PageHeader.defaultProps = {
    validationState: 'no-validation',
    children: null
};

export {
    PageHeader
};
