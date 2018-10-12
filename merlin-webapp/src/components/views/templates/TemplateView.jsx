import React from 'react';

class TemplateView extends React.Component {

    render = () => {
        return <div>
            Loading {this.props.match.params.templateId}
        </div>;
    };

    constructor(props) {
        super(props);
    }
}

export default TemplateView;