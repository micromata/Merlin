import React from 'react';
import {PageHeader} from 'react-bootstrap';
import DropArea from '../general/droparea/Component';

class DropAreaExample extends React.Component {

    render() {
        return (
            <div>
                <PageHeader>
                    Drop Area Example
                </PageHeader>

                <DropArea multiple/>
            </div>
        );
    }
}

export default DropAreaExample;
