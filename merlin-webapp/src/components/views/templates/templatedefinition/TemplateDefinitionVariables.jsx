import React from 'react';

class TemplateDefinitionVariables extends React.Component {
    render = () => {
        if (!this.props.definition.variableDefinitions) {
            return null;
        }
        const rows = [];
        this.props.definition.variableDefinitions.forEach((variable) => {
            rows.push(
                <div key={variable.name}>{variable.name}</div>
            );
        });
        return <div>
            {rows}
        </div>;
    };
}

export default TemplateDefinitionVariables;