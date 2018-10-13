import React from 'react';

class TemplateDefinition extends React.Component {

    render = () => {
        return <div>
            Loading {this.props.match.params.primaryKey}
        </div>;
    };

    constructor(props) {
        super(props);
    }
}

export default TemplateDefinition;