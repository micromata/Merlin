import React from 'react';

import {Card, CardBody, CardTitle, CardHeader, CardSubtitle, CardText, CardFooter} from 'reactstrap';

class TemplateCard extends React.Component {
    render = () => {

        return <div>
            <Card outline color="success" className={'template'} style={{ backgroundColor: '#fff', width: '20em' }}>
                <CardHeader>{this.props.id}</CardHeader>
                <CardBody>
                    <CardText>{this.props.description}</CardText>
                </CardBody>
                <CardFooter>Click to run.</CardFooter>
            </Card>
        </div>
    };

    constructor(props) {
        super(props);
    }
}

export default TemplateCard;