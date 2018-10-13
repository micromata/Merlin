import React from 'react';
import {Link} from 'react-router-dom';
import {Card, CardBody, CardFooter, CardHeader, CardText} from 'reactstrap';

class TemplateCard extends React.Component {

    buildItem = (label, content) => {
        return <li className="list-group-item"><b>{label}</b>{content.map((line, index) => {
            return <div key={index}>{line[0]}<br/><span style={{fontStyle: 'italic', color: 'grey'}}>{line[1]}</span>
            </div>;
        })}</li>;
    }

    render = () => {
        let templateId = null;
        this.props.template.fileDescriptor.filename;
        const definition = this.props.definition;
        let definitionText = null;
        if (!definition.autoGenerated) {
            let content = [['refid', definition.id]];
            if (definition.description) {
                content.push(['Description', definition.description]);
            }
            if (definition.fileDescriptor.filename) {
                content.push(['Filename', definition.fileDescriptor.filename]);
            }
            definitionText = this.buildItem('Definition', content);
        }

        return <div>
            <Link to={`/templates/${this.props.id}`} className={'card-link'}>
                <Card outline color="success" className={'template'} style={{backgroundColor: '#fff', width: '20em'}}>
                    <CardHeader>{this.props.template.fileDescriptor.filename}</CardHeader>
                    <ul className="list-group list-group-flush">
                        {definitionText}
                    </ul>
                    <CardFooter>Click to run.</CardFooter>
                </Card>
            </Link>
        </div>
    };

    constructor(props) {
        super(props);

        this.buildItem = this.buildItem.bind(this);
    }
}

export default TemplateCard;