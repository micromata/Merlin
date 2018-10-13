import React from 'react';
import {Link} from 'react-router-dom';
import {Card, CardFooter, CardHeader} from 'reactstrap';

class TemplateDefinitionCard extends React.Component {

    buildItem = (label, content) => {
        return <li className="list-group-item">{label}{content.map((line, index) => {
            let newLine = null;
            if (line[2] === 'description') {
                newLine = <br/>;
            }
            return <div className="card-list-entry" key={index}>{line[0]}:{newLine} <span
                className={`card-list-entry-value ${line[2]}`}>{line[1]}</span>
            </div>;
        })}</li>;
    }

    render = () => {
        const definition = this.props.definition;
        let definitionText = null;
        let content = [['refid', definition.id]];
        if (definition.fileDescriptor.filename) {
            content.push(['Filename', definition.fileDescriptor.filename, 'filename']);
        }
        if (definition.description) {
            content.push(['Description', definition.description, 'description']);
        }
        definitionText = this.buildItem('Info', content);
        return <div>
            <Link to={`/templates/${definition.primaryKey}`} className={'card-link'}>
                <Card outline color="success" className={'template'} style={{backgroundColor: '#fff', width: '20em'}}>
                    <CardHeader>{definition.id}</CardHeader>
                    <ul className="list-group list-group-flush">
                        {definitionText}
                    </ul>
                    <CardFooter>...</CardFooter>
                </Card>
            </Link>
        </div>
    };

    constructor(props) {
        super(props);

        this.buildItem = this.buildItem.bind(this);
    }
}

export default TemplateDefinitionCard;