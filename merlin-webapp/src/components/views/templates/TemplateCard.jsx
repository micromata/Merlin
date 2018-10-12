import React from 'react';
import {Link} from 'react-router-dom';
import {Card, CardBody, CardFooter, CardHeader, CardText} from 'reactstrap';

class TemplateCard extends React.Component {
    render = () => {

        return <div>
            <Link to={`/templates/${this.props.id}`} className={'card-link'}>
                <Card outline color="success" className={'template'} style={{backgroundColor: '#fff', width: '20em'}}>
                    <CardHeader>{this.props.id}</CardHeader>
                    <CardBody>
                        <CardText>{this.props.description}</CardText>
                    </CardBody>
                    <CardFooter>Click to run.</CardFooter>
                </Card>
            </Link>
        </div>
    };

    constructor(props) {
        super(props);
    }
}

export default TemplateCard;